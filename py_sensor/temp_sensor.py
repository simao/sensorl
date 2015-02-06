#!/usr/bin/python
# Can enable debug output by uncommenting:
#import logging
#logging.basicConfig(level=logging.DEBUG)

import time
import Adafruit_MCP9808.MCP9808 as MCP9808
import sys
sys.path.append('.')
import socket
import messages_pb2
import struct
from datetime import datetime

sensor = MCP9808.MCP9808()
sensor.begin()

def send_message(sock, message):
    """ Send a serialized message (protobuf Message interface)
    to a socket, prepended by its length packed in 4
    bytes (big endian).
    """
    s = message.SerializeToString()
    packed_len = struct.pack('>L', len(s))
    sock.sendall(packed_len)
    sock.sendall(s)

def build_msg(temp):
    m = messages_pb2.Measurement()
    m.mid = 0
    m.value = temp
    m.time = datetime.utcnow().isoformat()
    return m

print sys.argv

socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
socket.connect((sys.argv[1], int(sys.argv[2])))

while True:
    temp = sensor.readTempC()
    send_message(socket, build_msg(temp))
    print 'Temperature: {0:0.3F}*C'.format(temp)
    time.sleep(5.0)

socket.close()
