package net.manikin.core

object TransObject {
  import scala.language.implicitConversions
  
  // An Id that identifies an Object O
  trait Id[+O] {
    def init: O

    def version(implicit ctx: Context) : Long = ctx(this).version
    def old_version(implicit ctx: Context) : Long = ctx.previous(this).version

    def obj(implicit ctx: Context): O = ctx(this).obj
    def old_obj(implicit ctx: Context): O = ctx.previous(this).obj

    def typeString = this.init.getClass.getName.replace("$", ".")
  }

  /*
   * A Message can be sent to an Object O with address Id, returning R
   *
   * There are four stages:
   *
   * 1) (pre)pre-condition: predicate over anything. MUST BE a pure function (no side effects)
   * 2) (app)application: returns a new version of an Object. MUST BE a pure function (used for Event sourcing)
   * 3) (eff)effects: can do anything, but should return R
   * 4) (pst)post-condition: predicate over anything, including previous states. MUST BE a pure function
  */

  trait Message[+O, +I <: Id[O], +R] {
    // context and this will be injected
    @volatile private[core] var thisVar: Id[_] = _ // vars cannot be covariant, so hack it
    @volatile private[core] var contextVar: Context = _

    implicit def context: Context = contextVar
    def self: I = thisVar.asInstanceOf[I]

    def pre: Boolean
    def app: O
    def eff: R
    def pst: Boolean

    def _retries_ : Int = context.retries
    def typeString : String = this.getClass.getName.replace("$", ".")
  }

  /*
   * A Context acts as a 'memory' to resolve Ids to Objects, and tracks all (versioned) Objects
   * To send a Message to an Object, there MUST always be an implicit Context in scope (Scala magic)
   * A Context implementation is optionally responsible for Transactional guarantees when a Message is committed
   */
  trait Context {
    def apply[O](id: Id[O]): VObject[O]
    def previous[O](id: Id[O]): VObject[O]
    def send[O, I <: Id[O], R](id: I, message: Message[O, I, R]): R

    def retries: Int
    def failure: Failure
  }

  // Objects are versioned by Contexts
  case class VObject[+O](version: Long, obj: O)

  // Things can go wrong and that's encapsulated as type of Failure, not an Exception
  trait Failure

  case class FailureException(f: Failure) extends Exception {
    override def toString = "FailureException(" + f + ")"
  }
  
  case class ExceptionFailure(t: Throwable) extends Failure {
    override def toString = "ExceptionFailure(" + t + ")"
  }

  implicit class IdSyntax[O](id: Id[O]) {
    def ![I <: Id[O], R](msg: Message[O, I, R])(implicit ctx: Context): R = ctx.send(id, msg)
  }
}