# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: pbft.proto

import sys
_b=sys.version_info[0]<3 and (lambda x:x) or (lambda x:x.encode('latin1'))
from google.protobuf import descriptor as _descriptor
from google.protobuf import message as _message
from google.protobuf import reflection as _reflection
from google.protobuf import symbol_database as _symbol_database
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()


from google.protobuf import empty_pb2 as google_dot_protobuf_dot_empty__pb2


DESCRIPTOR = _descriptor.FileDescriptor(
  name='pbft.proto',
  package='',
  syntax='proto3',
  serialized_options=None,
  serialized_pb=_b('\n\npbft.proto\x1a\x1bgoogle/protobuf/empty.proto\"n\n\x0ePbftPreprepare\x12\n\n\x02sn\x18\x01 \x01(\x05\x12\x0c\n\x04view\x18\x02 \x01(\x05\x12\x0e\n\x06leader\x18\x03 \x01(\x05\x12\x15\n\x05\x62\x61tch\x18\x04 \x01(\x0b\x32\x06.Batch\x12\x0f\n\x07\x61\x62orted\x18\x05 \x01(\x08\x12\n\n\x02ts\x18\x06 \x01(\x03\"C\n\x0bPbftPrepare\x12\n\n\x02sn\x18\x01 \x01(\x05\x12\x0c\n\x04view\x18\x02 \x01(\x05\x12\x0e\n\x06\x64igest\x18\x03 \x01(\x0c\x12\n\n\x02ts\x18\x04 \x01(\x03\"B\n\nPbftCommit\x12\n\n\x02sn\x18\x01 \x01(\x05\x12\x0c\n\x04view\x18\x02 \x01(\x05\x12\x0e\n\x06\x64igest\x18\x03 \x01(\x0c\x12\n\n\x02ts\x18\x04 \x01(\x03\"c\n\rClientRequest\x12\x1e\n\nrequest_id\x18\x01 \x01(\x0b\x32\n.RequestID\x12\x0f\n\x07payload\x18\x02 \x01(\x0c\x12\x0e\n\x06pubkey\x18\x03 \x01(\x0c\x12\x11\n\tsignature\x18\x04 \x01(\x0c\"1\n\tRequestID\x12\x11\n\tclient_id\x18\x01 \x01(\x05\x12\x11\n\tclient_sn\x18\x02 \x01(\x05\")\n\x05\x42\x61tch\x12 \n\x08requests\x18\x01 \x03(\x0b\x32\x0e.ClientRequest2\xad\x01\n\x0bPbftService\x12\x38\n\rGetPreprepare\x12\x0f.PbftPreprepare\x1a\x16.google.protobuf.Empty\x12\x32\n\nGetPrepare\x12\x0c.PbftPrepare\x1a\x16.google.protobuf.Empty\x12\x30\n\tGetCommit\x12\x0b.PbftCommit\x1a\x16.google.protobuf.Emptyb\x06proto3')
  ,
  dependencies=[google_dot_protobuf_dot_empty__pb2.DESCRIPTOR,])




_PBFTPREPREPARE = _descriptor.Descriptor(
  name='PbftPreprepare',
  full_name='PbftPreprepare',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='sn', full_name='PbftPreprepare.sn', index=0,
      number=1, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='view', full_name='PbftPreprepare.view', index=1,
      number=2, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='leader', full_name='PbftPreprepare.leader', index=2,
      number=3, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='batch', full_name='PbftPreprepare.batch', index=3,
      number=4, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='aborted', full_name='PbftPreprepare.aborted', index=4,
      number=5, type=8, cpp_type=7, label=1,
      has_default_value=False, default_value=False,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='ts', full_name='PbftPreprepare.ts', index=5,
      number=6, type=3, cpp_type=2, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=43,
  serialized_end=153,
)


_PBFTPREPARE = _descriptor.Descriptor(
  name='PbftPrepare',
  full_name='PbftPrepare',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='sn', full_name='PbftPrepare.sn', index=0,
      number=1, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='view', full_name='PbftPrepare.view', index=1,
      number=2, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='digest', full_name='PbftPrepare.digest', index=2,
      number=3, type=12, cpp_type=9, label=1,
      has_default_value=False, default_value=_b(""),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='ts', full_name='PbftPrepare.ts', index=3,
      number=4, type=3, cpp_type=2, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=155,
  serialized_end=222,
)


_PBFTCOMMIT = _descriptor.Descriptor(
  name='PbftCommit',
  full_name='PbftCommit',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='sn', full_name='PbftCommit.sn', index=0,
      number=1, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='view', full_name='PbftCommit.view', index=1,
      number=2, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='digest', full_name='PbftCommit.digest', index=2,
      number=3, type=12, cpp_type=9, label=1,
      has_default_value=False, default_value=_b(""),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='ts', full_name='PbftCommit.ts', index=3,
      number=4, type=3, cpp_type=2, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=224,
  serialized_end=290,
)


_CLIENTREQUEST = _descriptor.Descriptor(
  name='ClientRequest',
  full_name='ClientRequest',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='request_id', full_name='ClientRequest.request_id', index=0,
      number=1, type=11, cpp_type=10, label=1,
      has_default_value=False, default_value=None,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='payload', full_name='ClientRequest.payload', index=1,
      number=2, type=12, cpp_type=9, label=1,
      has_default_value=False, default_value=_b(""),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='pubkey', full_name='ClientRequest.pubkey', index=2,
      number=3, type=12, cpp_type=9, label=1,
      has_default_value=False, default_value=_b(""),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='signature', full_name='ClientRequest.signature', index=3,
      number=4, type=12, cpp_type=9, label=1,
      has_default_value=False, default_value=_b(""),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=292,
  serialized_end=391,
)


_REQUESTID = _descriptor.Descriptor(
  name='RequestID',
  full_name='RequestID',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='client_id', full_name='RequestID.client_id', index=0,
      number=1, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
    _descriptor.FieldDescriptor(
      name='client_sn', full_name='RequestID.client_sn', index=1,
      number=2, type=5, cpp_type=1, label=1,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=393,
  serialized_end=442,
)


_BATCH = _descriptor.Descriptor(
  name='Batch',
  full_name='Batch',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    _descriptor.FieldDescriptor(
      name='requests', full_name='Batch.requests', index=0,
      number=1, type=11, cpp_type=10, label=3,
      has_default_value=False, default_value=[],
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      serialized_options=None, file=DESCRIPTOR),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  serialized_options=None,
  is_extendable=False,
  syntax='proto3',
  extension_ranges=[],
  oneofs=[
  ],
  serialized_start=444,
  serialized_end=485,
)

_PBFTPREPREPARE.fields_by_name['batch'].message_type = _BATCH
_CLIENTREQUEST.fields_by_name['request_id'].message_type = _REQUESTID
_BATCH.fields_by_name['requests'].message_type = _CLIENTREQUEST
DESCRIPTOR.message_types_by_name['PbftPreprepare'] = _PBFTPREPREPARE
DESCRIPTOR.message_types_by_name['PbftPrepare'] = _PBFTPREPARE
DESCRIPTOR.message_types_by_name['PbftCommit'] = _PBFTCOMMIT
DESCRIPTOR.message_types_by_name['ClientRequest'] = _CLIENTREQUEST
DESCRIPTOR.message_types_by_name['RequestID'] = _REQUESTID
DESCRIPTOR.message_types_by_name['Batch'] = _BATCH
_sym_db.RegisterFileDescriptor(DESCRIPTOR)

PbftPreprepare = _reflection.GeneratedProtocolMessageType('PbftPreprepare', (_message.Message,), dict(
  DESCRIPTOR = _PBFTPREPREPARE,
  __module__ = 'pbft_pb2'
  # @@protoc_insertion_point(class_scope:PbftPreprepare)
  ))
_sym_db.RegisterMessage(PbftPreprepare)

PbftPrepare = _reflection.GeneratedProtocolMessageType('PbftPrepare', (_message.Message,), dict(
  DESCRIPTOR = _PBFTPREPARE,
  __module__ = 'pbft_pb2'
  # @@protoc_insertion_point(class_scope:PbftPrepare)
  ))
_sym_db.RegisterMessage(PbftPrepare)

PbftCommit = _reflection.GeneratedProtocolMessageType('PbftCommit', (_message.Message,), dict(
  DESCRIPTOR = _PBFTCOMMIT,
  __module__ = 'pbft_pb2'
  # @@protoc_insertion_point(class_scope:PbftCommit)
  ))
_sym_db.RegisterMessage(PbftCommit)

ClientRequest = _reflection.GeneratedProtocolMessageType('ClientRequest', (_message.Message,), dict(
  DESCRIPTOR = _CLIENTREQUEST,
  __module__ = 'pbft_pb2'
  # @@protoc_insertion_point(class_scope:ClientRequest)
  ))
_sym_db.RegisterMessage(ClientRequest)

RequestID = _reflection.GeneratedProtocolMessageType('RequestID', (_message.Message,), dict(
  DESCRIPTOR = _REQUESTID,
  __module__ = 'pbft_pb2'
  # @@protoc_insertion_point(class_scope:RequestID)
  ))
_sym_db.RegisterMessage(RequestID)

Batch = _reflection.GeneratedProtocolMessageType('Batch', (_message.Message,), dict(
  DESCRIPTOR = _BATCH,
  __module__ = 'pbft_pb2'
  # @@protoc_insertion_point(class_scope:Batch)
  ))
_sym_db.RegisterMessage(Batch)



_PBFTSERVICE = _descriptor.ServiceDescriptor(
  name='PbftService',
  full_name='PbftService',
  file=DESCRIPTOR,
  index=0,
  serialized_options=None,
  serialized_start=488,
  serialized_end=661,
  methods=[
  _descriptor.MethodDescriptor(
    name='GetPreprepare',
    full_name='PbftService.GetPreprepare',
    index=0,
    containing_service=None,
    input_type=_PBFTPREPREPARE,
    output_type=google_dot_protobuf_dot_empty__pb2._EMPTY,
    serialized_options=None,
  ),
  _descriptor.MethodDescriptor(
    name='GetPrepare',
    full_name='PbftService.GetPrepare',
    index=1,
    containing_service=None,
    input_type=_PBFTPREPARE,
    output_type=google_dot_protobuf_dot_empty__pb2._EMPTY,
    serialized_options=None,
  ),
  _descriptor.MethodDescriptor(
    name='GetCommit',
    full_name='PbftService.GetCommit',
    index=2,
    containing_service=None,
    input_type=_PBFTCOMMIT,
    output_type=google_dot_protobuf_dot_empty__pb2._EMPTY,
    serialized_options=None,
  ),
])
_sym_db.RegisterServiceDescriptor(_PBFTSERVICE)

DESCRIPTOR.services_by_name['PbftService'] = _PBFTSERVICE

# @@protoc_insertion_point(module_scope)
