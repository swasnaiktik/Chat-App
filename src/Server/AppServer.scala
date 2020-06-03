package Server
import ClientActor.ClientActor
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.corundumstudio.socketio.listener.DataListener
import com.corundumstudio.socketio.{AckRequest, Configuration, SocketIOClient, SocketIOServer}
import scala.io.Source
import com.corundumstudio.socketio.listener.DataListener


class AppServer extends Actor {
  var mapUserActor: Map[String, ActorRef] = Map()
  var mapUserSocket: Map[String, SocketIOClient] = Map()
  var mapActorUser: Map[ActorRef, String] = Map()
  var mapSocketUser: Map[SocketIOClient, String] = Map()

  val config: Configuration = new Configuration{
    setHostname("localHost")
    setPort(8080)
  }

  val server: SocketIOServer = new SocketIOServer(config)
  server.addEventListener("addUserName", classOf[String], new addUser(this))
  server.addEventListener("sendMessage", classOf[String], new sendMessage(this))
  server.addEventListener("UserJoined", classOf[Nothing], new Joined(this))

  server.start()

  def receive: Receive = {
    case test => None
  }
}

class addUser(server: AppServer) extends DataListener[String]{
  override def onData(client: SocketIOClient, data: String, ackSender: AckRequest): Unit = {
    val UserActor: ActorRef = server.context.actorOf(Props(classOf[ClientActor], data))
    server.mapUserActor += (data -> UserActor)
    server.mapUserSocket += (data -> client)
    server.mapActorUser += (UserActor -> data)
    server.mapSocketUser += (client -> data)

    client.sendEvent("StartChat", "")
  }
}

class sendMessage(server: AppServer) extends DataListener[String]{
  override def onData(client: SocketIOClient, data: String, ackSender: AckRequest): Unit = {
    for((username, socket) <- server.mapUserSocket){
      socket.sendEvent("messageReceived", server.mapSocketUser.getOrElse(client, None) + ": " + data)
    }
  }
}

class Joined(server: AppServer) extends DataListener[Nothing]{
  override def onData(client: SocketIOClient, data: Nothing, ackSender: AckRequest): Unit = {
    for((username, socket) <- server.mapUserSocket){
      socket.sendEvent("JoiningMessage", server.mapSocketUser.getOrElse(client, None) + " joined the chat")
    }
  }
}

object AppServer{
  def main(args: Array[String]): Unit = {
    val actorSystem = ActorSystem()
    val server = actorSystem.actorOf(Props(classOf[AppServer]))
  }
}