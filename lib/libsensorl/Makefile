JAVA_HOME := /usr/lib/jvm/jdk1.8.0_45
GCC := gcc
CFLAGS := -fPIC -c
DEBUG_FLAGS := -g

all: libsensorl.so

%.o: %.c
	$(GCC) $(DEBUG_FLAGS) $(CFLAGS) $< -I $(JAVA_HOME)/include -I $(JAVA_HOME)/include/linux

libsensorl.so: *.o
	$(GCC) $(DEBUG_FLAGS) $< -lrrd -shared -o $@ -Wl,-soname,sensorl

clean:
	rm -f *.o *.so
