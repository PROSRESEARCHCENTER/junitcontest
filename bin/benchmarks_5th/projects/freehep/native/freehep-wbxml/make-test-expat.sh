#!/bin/sh
g++ -I./expat-2.0.1/lib -I./src/main/include -I./src/test/include -g -O2 -Wall -DHAVE_EXPAT_CONFIG_H -o TestExpat.o -c TestExpat.cpp
/bin/sh ./expat-2.0.1/libtool --silent --mode=link g++ -I./expat-2.0.1/lib -g -O2 -Wall -fexceptions  -DHAVE_EXPAT_CONFIG_H -o TestExpat TestExpat.o ./expat-2.0.1/libexpat.la ./target/nar/lib/i386-MacOSX-g++/static/libfreehep-wbxml-0.6-SNAPSHOT.a ./target/test-nar/obj/i386-MacOSX-g++/TestContentHandler.o
