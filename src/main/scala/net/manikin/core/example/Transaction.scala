package net.manikin.core.example

// Plain vanilla Transaction (no annotations)
object Transaction {
  import net.manikin.core.asm.AbstractStateMachine._
  import net.manikin.core.example.Account._

  case class Id  (id: Long) extends StateID[Data] { def initData = Data() }
  case class Data(from: Account.Id = null, to: Account.Id = null, amount: Double = 0.0)

  trait Trs extends StateTransition[Data]

  case class Create(from: Account.Id, to: Account.Id, amount: Double) extends Trs {
    def nst =   Map("Initial" -> "Created")
    def pre =   from().state == "Opened" && to().state == "Opened"
    def ap2 =   data() = data().copy(from = from, to = to, amount = amount)
    def pst =   data().from == from && data().to == to && data().amount == amount
  }

  case class Commit() extends Trs {
    def amt =   data().amount
    def from =  data().from
    def to =    data().to
    
    def nst =   Map("Created" -> "Committed")
    def pre =   true
    def ap2 =   { Withdraw(amt) --> from ; Deposit(amt) --> to }
    def pst =   from.previous.data.balance + to.previous.data.balance == from().data.balance + to().data.balance
  }  
}
