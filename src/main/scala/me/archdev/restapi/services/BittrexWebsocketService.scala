package me.archdev.restapi.services

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import me.archdev.restapi.utils.{Config, DatabaseService}

import scala.concurrent.{ExecutionContext, Future, Promise}

class BittrexWebsocketService(val databaseService: DatabaseService)
                             (implicit executionContext: ExecutionContext,
                              system: ActorSystem,
                              materializer: ActorMaterializer) extends Config {

  val endpoint = bittrex.getString("websocket")
  val incoming: Sink[Message, Future[Done]] =
    Sink.foreach[Message] {
      case message: TextMessage.Strict =>
        println(message.text)
      case message =>
        println(message)
    }

  // using Source.maybe materializes into a promise
  // which will allow us to complete the source later
  val flow: Flow[Message, Message, Promise[Option[Message]]] =
  Flow.fromSinkAndSourceMat(
    incoming,
    Source.maybe[Message])(Keep.right)

  val (upgradeResponse, promise) =
    Http().singleWebSocketRequest(
      WebSocketRequest(endpoint),
      flow)

  def connect(): Unit = {
  }

  def disconnect() = {
    promise.success(None)
  }
}