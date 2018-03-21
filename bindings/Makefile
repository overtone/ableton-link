JAVA_DIR = $(wildcard /usr/java/jdk*/include)

ifeq ($(OS),Windows_NT)
    PLATFORM='LINK_PLATFORM_WINDOWS=1'
    OUT_LIB_FILE = '../native/windows/x86_64/abletonlink.dll'

else ifeq ($(UNAME_S),Darwin)
    PLATFORM='LINK_PLATFORM_MACOSX=1'
    OUT_LIB_FILE = '../native/macosx/x86_64/libabletonlink.so'

else
    PLATFORM= 'LINK_PLATFORM_LINUX=1'
    OUT_LIB_FILE = '../native/linux/x86_64/libabletonlink.so'
endif

build:
	g++ -fPIC -I$(JAVA_DIR) -I$(JAVA_DIR)/linux -I../link/include -I../link/modules/asio-standalone/asio/include -D$(PLATFORM) -shared -o $(OUT_LIB_FILE) link_bindings.cc

clean:
	rm $OUT_LIB_FILE
