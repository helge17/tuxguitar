# See pom.xml for variable definitions

OBJECTS=\
	LV2Logger.o\
	LV2Lock.o\
	LV2Client.o\
	LV2World.o\
	LV2Plugin.o\
	LV2Worker.o\
	LV2Feature.o\
	LV2Instance.o\
	LV2UIQt5.o\
	LV2Socket.o\

%.o: %.c
	$(CXX) $(CFLAGS) -c -o $@ $<

all:    $(BINARY)

$(BINARY):	$(OBJECTS)
	$(CXX) $(LDFLAGS) -o $(BINARY) $(OBJECTS) $(LDPATH) $(LDLIBS)

clean:
	$(RM) $(OBJECTS) $(BINARY)
