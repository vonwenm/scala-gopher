package gopher.channels

import scala.concurrent._
import scala.util._
import gopher._

trait PromiseFlowTermination[A] extends FlowTermination[A]
{

  def doThrow(e: Throwable): Unit =
  {
    if (isCompleted) {
      import ExecutionContext.Implicits.global
      p.future.onComplete{ 
         case Success(x) =>
           // success was before throw, ignoring.
         case Failure(prevEx) =>
          //System.err.println("previous exception:"+prevEx)
          //prevEx.printStackTrace();
      }
    } else {
      p failure e
   }
  }

  def doExit(a: A): Unit =
    p success a

  def defer(body: =>Unit)(implicit ec: ExecutionContext): Unit =
    p.future.onComplete{ x => body }

  def future =
    p future

  def isCompleted = p.isCompleted

  def throwIfNotCompleted(ex: Throwable):Unit =
      p.tryFailure(ex.fillInStackTrace())

  val p = Promise[A]()

}

object PromiseFlowTermination
{
  def apply[A]() = new PromiseFlowTermination[A]() {}
}