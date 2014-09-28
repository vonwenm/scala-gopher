package gopher.channels

import gopher._
import scala.concurrent._

sealed trait ReadSelectorArgument[A,B]
{
  def normalizedFun: ContRead[A,B] => Option[(()=>A)=>Future[Continuated[B]]]
}

case class AsyncFullReadSelectorArgument[A,B](
                   f: ContRead[A,B] => Option[(()=>A)=>Future[Continuated[B]]]
              )  extends ReadSelectorArgument[A,B]
{
  def normalizedFun = f
}

case class AsyncNoOptionReadSelectorArgument[A,B](
                   f: ContRead[A,B] => ((()=>A)=>Future[Continuated[B]])
               ) extends ReadSelectorArgument[A,B]
{
   def normalizedFun = ( c => Some(f(c)) )
}

case class AsyncNoGenReadSelectorArgument[A,B](
                   f: ContRead[A,B] => (A=>Future[Continuated[B]])
               ) extends ReadSelectorArgument[A,B]
{
   def normalizedFun = ( c => Some(gen => f(c)(gen())) )
}

case class AsyncPairReadSelectorArgument[A,B](
                   f: (A, ContRead[A,B]) => Future[Continuated[B]]
               ) extends ReadSelectorArgument[A,B]
{
   def normalizedFun = ( c => Some(gen => f(gen(),c)) ) 
}


case class SyncReadSelectorArgument[A,B](
                   f: ContRead[A,B] => ((()=>A) => Continuated[B])
               ) extends ReadSelectorArgument[A,B]
{
  def normalizedFun = ( c => Some( gen => Future successful f(c)(gen) ) )
}

case class SyncPairReadSelectorArgument[A,B](
                   f: (A, ContRead[A,B]) => Continuated[B]
               ) extends ReadSelectorArgument[A,B]
{
   def normalizedFun = ( c => Some(gen => Future successful f(gen(),c)) )
}

sealed trait WriteSelectorArgument[A,B]
{
  def normalizedFun: ContWrite[A,B] => Option[(A,Future[Continuated[B]])]
}

case class AsyncFullWriteSelectorArgument[A,B](
                   f: ContWrite[A,B] => Option[(A,Future[Continuated[B]])]
              )  extends WriteSelectorArgument[A,B]
{
  def normalizedFun = f
}

case class AsyncNoOptWriteSelectorArgument[A,B](
                   f: ContWrite[A,B] => (A,Future[Continuated[B]])
              )  extends WriteSelectorArgument[A,B]
{
  def normalizedFun = (c => Some(f(c)))
}

case class SyncWriteSelectorArgument[A,B](
                   f: ContWrite[A,B] => (A,Continuated[B])
              )  extends WriteSelectorArgument[A,B]
{
  def normalizedFun = {c => 
     val (a, next) = f(c) 
     Some((a,Future successful next))
  }

}

sealed trait SkipSelectorArgument[A]
{
  def normalizedFun: Skip[A] => Option[Future[Continuated[A]]]
}

case class AsyncFullSkipSelectorArgument[A](
                   f: Skip[A] => Option[Future[Continuated[A]]]
              )  extends SkipSelectorArgument[A]
{
  def normalizedFun = f
}

case class AsyncNoOptSkipSelectorArgument[A](
                   f: Skip[A] => Future[Continuated[A]]
              )  extends SkipSelectorArgument[A]
{
  def normalizedFun = { c => Some(f(c)) }
}

case class SyncSelectorArgument[A](
                   f: Skip[A] => Continuated[A]
              )  extends SkipSelectorArgument[A]
{
  def normalizedFun = { c => Some(Future successful f(c)) }
}

