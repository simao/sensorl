# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: messages.proto

from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import descriptor_pb2
# @@protoc_insertion_point(imports)




DESCRIPTOR = _descriptor.FileDescriptor(
  name='messages.proto',
  package='io.simao.sensorl.message',
  serialized_pb='\n\x0emessages.proto\x12\x18io.simao.sensorl.message\"D\n\x0bMeasurement\x12\x0b\n\x03mid\x18\x01 \x02(\r\x12\r\n\x05value\x18\x02 \x02(\x01\x12\x0c\n\x04time\x18\x03 \x02(\t\x12\x0b\n\x03key\x18\x04 \x02(\t')




_MEASUREMENT = _descriptor.Descriptor(
  name='Measurement',
  full_name='io.simao.sensorl.message.Measurement',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='mid', full_name='io.simao.sensorl.message.Measurement.mid', index=0,
      number=1, type=13, cpp_type=3, label=2,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    _descriptor.FieldDescriptor(
      name='value', full_name='io.simao.sensorl.message.Measurement.value', index=1,
      number=2, type=1, cpp_type=5, label=2,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    _descriptor.FieldDescriptor(
      name='time', full_name='io.simao.sensorl.message.Measurement.time', index=2,
      number=3, type=9, cpp_type=9, label=2,
      has_default_value=False, default_value=unicode("", "utf-8"),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    _descriptor.FieldDescriptor(
      name='key', full_name='io.simao.sensorl.message.Measurement.key', index=3,
      number=4, type=9, cpp_type=9, label=2,
      has_default_value=False, default_value=unicode("", "utf-8"),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  options=None,
  is_extendable=False,
  extension_ranges=[],
  serialized_start=44,
  serialized_end=112,
)

DESCRIPTOR.message_types_by_name['Measurement'] = _MEASUREMENT

class Measurement(_message.Message):
  __metaclass__ = _reflection.GeneratedProtocolMessageType
  DESCRIPTOR = _MEASUREMENT

  # @@protoc_insertion_point(class_scope:io.simao.sensorl.message.Measurement)


# @@protoc_insertion_point(module_scope)
