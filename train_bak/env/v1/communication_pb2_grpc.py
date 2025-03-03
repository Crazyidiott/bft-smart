# Generated by the gRPC Python protocol compiler plugin. DO NOT EDIT!
import grpc

import communication_pb2 as communication__pb2


class CommunicationServiceStub(object):
  # missing associated documentation comment in .proto file
  pass

  def __init__(self, channel):
    """Constructor.

    Args:
      channel: A grpc.Channel.
    """
    self.SendMessage = channel.unary_unary(
        '/communication.CommunicationService/SendMessage',
        request_serializer=communication__pb2.Message.SerializeToString,
        response_deserializer=communication__pb2.Message.FromString,
        )


class CommunicationServiceServicer(object):
  # missing associated documentation comment in .proto file
  pass

  def SendMessage(self, request, context):
    # missing associated documentation comment in .proto file
    pass
    context.set_code(grpc.StatusCode.UNIMPLEMENTED)
    context.set_details('Method not implemented!')
    raise NotImplementedError('Method not implemented!')


def add_CommunicationServiceServicer_to_server(servicer, server):
  rpc_method_handlers = {
      'SendMessage': grpc.unary_unary_rpc_method_handler(
          servicer.SendMessage,
          request_deserializer=communication__pb2.Message.FromString,
          response_serializer=communication__pb2.Message.SerializeToString,
      ),
  }
  generic_handler = grpc.method_handlers_generic_handler(
      'communication.CommunicationService', rpc_method_handlers)
  server.add_generic_rpc_handlers((generic_handler,))
