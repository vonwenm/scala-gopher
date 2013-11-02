package gopher.channels

import scala.concurrent._
import akka.actor._

import ops._


class InputChannelOps[API <: ChannelsAPI[API],A](val ch: API#IChannel[A]) extends AnyVal
{

  
  def readZipped[B](c:Iterable[B])(f: (A,B)=>Unit)(implicit api: ChannelsAPI[API], ec: ExecutionContext, as: ActorSystem = ChannelsActorSystemStub.defaultSystem): Tie[API]  = 
  {
    api.makeTie.addReadAction(ch, new ReadZipped(c.iterator,f)).start()
  }
  
  def readWhile(p: A => Boolean)(f: A => Unit)(implicit api: ChannelsAPI[API], ec: ExecutionContext, as: ActorSystem = ChannelsActorSystemStub.defaultSystem): Tie[API] =
  {
   api.makeTie.addReadAction(ch, new ReadWhile(p,f)).start() 
  }
  
  def foldWhile[S](s:S)(p: (A,S)=>Boolean)(f:(A,S)=>S)(implicit api: ChannelsAPI[API], ec: ExecutionContext, as: ActorSystem = ChannelsActorSystemStub.defaultSystem): Future[S] = 
  {
    val promise = Promise[S]();
    api.makeTie.addReadAction(ch,new FoldWhile(s,p,f,promise)).start();
    promise.future
  }

  def ffoldWhile[S](s:S)(p: (A,S)=>Boolean)(f:(A,S)=>Future[S])(implicit api: ChannelsAPI[API], ec: ExecutionContext, as: ActorSystem = ChannelsActorSystemStub.defaultSystem): Future[S] = 
  {
    val promise = Promise[S]();
    api.makeTie.addReadAction(ch,new FFoldWhile(s,p,f,promise)).start();
    promise.future
  }
  
  //TODO:
  //def ffold[S](s:S)(f:(A,S)=>Future[Option[S]]):Future[S] = ???
  
  
  
}