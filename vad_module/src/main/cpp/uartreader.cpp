#include <jni.h>
#include <string>
#include <thread>
#include <atomic>
#include <vector>
#include <fcntl.h>
#include <unistd.h>
#include <termios.h>
#include <errno.h>
#include <cstring>

static JavaVM* gVm = nullptr;

static jobject gCallback = nullptr; // Global ref to UartNative.Callback
static jmethodID gOnCmdMid = nullptr; // void onCmd(int)
static jmethodID gOnErrMid = nullptr; // void onError(String)

static std::thread gThread;
static std::atomic<bool> gRunning{false};

static speed_t baudToSpeed(int baud) {
    switch (baud) {
        case 9600: return B9600;
        case 19200: return B19200;
        case 38400: return B38400;
        case 57600: return B57600;
        case 115200: return B115200;
        default: return B9600;
    }
}

static void callOnCmd(JNIEnv* env, int cmdId) {
    if (!gCallback || !gOnCmdMid) return;
    env->CallVoidMethod(gCallback, gOnCmdMid, (jint)cmdId);
    if (env->ExceptionCheck()) env->ExceptionClear();
}

static void callOnError(JNIEnv* env, const std::string& msg) {
    if (!gCallback || !gOnErrMid) return;
    jstring jmsg = env->NewStringUTF(msg.c_str());
    env->CallVoidMethod(gCallback, gOnErrMid, jmsg);
    if (env->ExceptionCheck()) env->ExceptionClear();
    env->DeleteLocalRef(jmsg);
}

static bool configureUart(int fd, int baud) {
    termios tty{};
    if (tcgetattr(fd, &tty) != 0) return false;

    cfsetispeed(&tty, baudToSpeed(baud));
    cfsetospeed(&tty, baudToSpeed(baud));

    tty.c_cflag = (tty.c_cflag & ~CSIZE) | CS8;
    tty.c_cflag |= (CLOCAL | CREAD);
    tty.c_cflag &= ~(PARENB | PARODD);
    tty.c_cflag &= ~CSTOPB;
    tty.c_cflag &= ~CRTSCTS;

    tty.c_iflag = IGNPAR;
    tty.c_oflag = 0;
    tty.c_lflag = 0;

    tty.c_cc[VMIN] = 1;
    tty.c_cc[VTIME] = 1;

    tcflush(fd, TCIFLUSH);
    return tcsetattr(fd, TCSANOW, &tty) == 0;
}

// Vendor checksum: sum bytes [0..5] and take low 8 bits
static uint8_t computeChecksum(const uint8_t* frame) {
    uint32_t sum = 0;
    for (int i = 0; i <= 5; i++) sum += frame[i];
    return (uint8_t)(sum & 0xFF);
}

static void readerLoop(const std::string& devPath, int baud) {
    JNIEnv* env = nullptr;
    if (gVm->AttachCurrentThread(&env, nullptr) != JNI_OK) return;

    int fd = open(devPath.c_str(), O_RDONLY | O_NOCTTY);
    if (fd < 0) {
        callOnError(env, "open failed: " + devPath +
                         " errno=" + std::to_string(errno) + " " + std::string(strerror(errno)));
        gVm->DetachCurrentThread();
        return;
    }

    if (!configureUart(fd, baud)) {
        callOnError(env, "configureUart failed errno=" + std::to_string(errno) + " " + std::string(strerror(errno)));
        close(fd);
        gVm->DetachCurrentThread();
        return;
    }

    std::vector<uint8_t> buf;
    buf.reserve(512);

    uint8_t tmp[128];

    while (gRunning.load()) {
        int n = (int)read(fd, tmp, sizeof(tmp));
        if (n <= 0) continue;

        buf.insert(buf.end(), tmp, tmp + n);

        while (buf.size() >= 8) {
            size_t start = 0;
            bool found = false;

            for (; start + 1 < buf.size(); start++) {
                if (buf[start] == 0xA5 && buf[start + 1] == 0xFA) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                buf.clear();
                break;
            }

            if (start > 0) {
                buf.erase(buf.begin(), buf.begin() + (long)start);
            }

            if (buf.size() < 8) break;

            if (buf[7] != 0xFB) {
                buf.erase(buf.begin());
                continue;
            }

            uint8_t expected = computeChecksum(buf.data());
            uint8_t got = buf[6];
            if (expected != got) {
                buf.erase(buf.begin());
                continue;
            }

            int cmdId = (buf[4] & 0xFF) | ((buf[5] & 0xFF) << 8);
            callOnCmd(env, cmdId);

            buf.erase(buf.begin(), buf.begin() + 8);
        }
    }

    close(fd);
    gVm->DetachCurrentThread();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_voice_1activation_1uart_UartNative_nativeStart(JNIEnv* env, jobject, jstring devicePath, jint baud) {
    if (gRunning.load()) return;

    const char* cpath = env->GetStringUTFChars(devicePath, nullptr);
    std::string path = cpath ? cpath : "/dev/ttyS0";
    if (cpath) env->ReleaseStringUTFChars(devicePath, cpath);

    gRunning.store(true);
    gThread = std::thread(readerLoop, path, (int)baud);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_voice_1activation_1uart_UartNative_nativeStop(JNIEnv*, jobject) {
    if (!gRunning.load()) return;
    gRunning.store(false);
    if (gThread.joinable()) gThread.join();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_voice_1activation_1uart_UartNative_nativeSetCallback(JNIEnv* env, jobject, jobject callback) {
    if (gCallback) {
        env->DeleteGlobalRef(gCallback);
        gCallback = nullptr;
    }
    gOnCmdMid = nullptr;
    gOnErrMid = nullptr;

    if (!callback) return;

    gCallback = env->NewGlobalRef(callback);

    jclass cbCls = env->GetObjectClass(callback);
    if (!cbCls) return;

    gOnCmdMid = env->GetMethodID(cbCls, "onCmd", "(I)V");
    gOnErrMid = env->GetMethodID(cbCls, "onError", "(Ljava/lang/String;)V");

    env->DeleteLocalRef(cbCls);
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
    gVm = vm;
    return JNI_VERSION_1_6;
}