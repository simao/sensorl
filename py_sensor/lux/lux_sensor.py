#!/usr/bin/python
# Can enable debug output by uncommenting:
#import logging
#logging.basicConfig(level=logging.DEBUG)

import time
import sys
sys.path.append('.')
sys.path.append('..')
import socket
import messages_pb2
import struct
from datetime import datetime

from TSL2561 import TSL2561

sensor = TSL2561()

def send_message(sock, message):
    """ Send a serialized message (protobuf Message interface)
    to a socket, prepended by its length packed in 4
    bytes (big endian).
    """
    s = message.SerializeToString()
    packed_len = struct.pack('>L', len(s))
    sock.sendall(packed_len)
    sock.sendall(s)

def build_msg(value, sensor_name):
    m = messages_pb2.Measurement()
    m.mid = 0
    m.value = value
    m.time = datetime.utcnow().isoformat()
    m.key = sensor_name
    return m

def sendAll(socket, sensor):
    lux = sensor.readLux()
    ambient = sensor.readFull()

    print 'Lux: {0:0.3F}'.format(lux)
    print 'Ambient: {0:0.3F}'.format(ambient)

    send_message(socket, build_msg(lux, "lux"))
    send_message(socket, build_msg(ambient, "ambient"))

print sys.argv

socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
socket.connect((sys.argv[1], int(sys.argv[2])))

while True:
    send_message(socket, sensor)
    time.sleep(3.0)

socket.close()
