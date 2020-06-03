package ClientActor
import akka.actor.Actor
case object test



class ClientActor(username: String) extends Actor {

  def receive: Receive ={
    case test => None

  }

}