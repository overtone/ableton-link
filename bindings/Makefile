UNAME := $(shell uname)

ifeq ($(UNAME),Darwin)
    PLATFORM='LINK_PLATFORM_MACOSX=1'
    OUT_LIB_FILE='../native/macosx/x86_64/libabletonlink.dylib'
    PROGRAM='clang++'
    SHARED_LIB_FLAG='-dynamiclib'
else
    PLATFORM='LINK_PLATFORM_LINUX=1'
    OUT_LIB_FILE='../native/linux/x86_64/libabletonlink.so'
    PROGRAM='g++'
    SHARED_LIB_FLAG='-shared -fPIC'
endif

build:
	$(PROGRAM) -I../link/include -I../link/modules/asio-standalone/asio/include -I/opt/local/include \
	-D$(PLATFORM) -DASIO_HAS_BOOST_DATE_TIME=0 -std=c++11 -stdlib=libc++ -Weverything -m64  \
	$(SHARED_LIB_FLAG) -o$(OUT_LIB_FILE) -DNDEBUG=1 link_bindings.cc

clean:
	rm $OUT_LIB_FILE
