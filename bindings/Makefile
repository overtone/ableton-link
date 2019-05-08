UNAME := $(shell uname)

ifeq ($(UNAME),Darwin)
    PLATFORM='LINK_PLATFORM_MACOSX=1'
    OUT_LIB_FILE='../native/macosx/x86_64/libabletonlink.dylib'
    PROGRAM='clang++'
    SHARED_LIB_FLAG='-dynamiclib'
    MAC_CPP='-std=c++14'
    MAC_LIB_CPP='-stdlib=libc++'
    MAC_ASIO_FIX='-DASIO_HAS_BOOST_DATE_TIME=0'
    MAC_EVERYTHING_FLAG='-Weverything'
else
    PLATFORM='LINK_PLATFORM_LINUX=1'
    OUT_LIB_FILE='../native/linux/x86_64/libabletonlink.so'
    PROGRAM='g++'
    SHARED_LIB_FLAG='-shared'
    SHARED_LIB_FLAG_LINUX='-fPIC'
endif

build:
	$(PROGRAM) -I../link/include -I../link/modules/asio-standalone/asio/include -I/opt/local/include \
	-D$(PLATFORM) $(MAC_ASIO_FIX) $(MAC_LIB_CPP) $(MAC_CPP) $(MAC_EVERYTHING_FLAG)  -m64  \
	$(SHARED_LIB_FLAG) $(SHARED_LIB_FLAG_LINUX) -o$(OUT_LIB_FILE) -DNDEBUG=1 link_bindings.cc

clean:
	rm $OUT_LIB_FILE
