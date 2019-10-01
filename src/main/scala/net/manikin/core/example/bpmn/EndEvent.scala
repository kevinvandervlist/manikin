package net.manikin.core.example.bpmn

object EndEvent {
  import java.util.UUID
  import net.manikin.core.example.bpmn.Element._

  case class EndEventData() 

  case class EndEventId(uuid: UUID = UUID.randomUUID()) extends ElementId[EndEventData] {
    def initElement = EndEventData()
  }
}