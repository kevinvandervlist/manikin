package net.manikin.core.context.store.slick

object PostgresTable {
  import slick.jdbc.PostgresProfile.api._

  class Transaction(tag: Tag) extends Table[(Long, Long, Int)](tag, "transaction") {
    def tx_uuid = column[Long]("tx_uuid")
    def tx_id = column[Long]("tx_id")
    def tx_size = column[Int]("tx_size")

    def pk = primaryKey("pk_a", (tx_uuid, tx_id))
    def * = (tx_uuid, tx_id, tx_size)
  }

  class Event(tag: Tag) extends Table[(Array[Byte], Long, Long, Long, Int, Int, Array[Byte], String, String)](tag, "event") {
    def id = column[Array[Byte]]("id")
    def event_id = column[Long]("event_id")
    def tx_uuid = column[Long]("tx_uuid")
    def tx_id = column[Long]("tx_id")
    def tx_depth = column[Int]("tx_depth")
    def tx_seq = column[Int]("tx_seq")
    def event = column[Array[Byte]]("event")
    def id_string = column[String]("id_string")
    def type_string = column[String]("type_string")

    def pk = primaryKey("pk_a", (id, event_id))
    def * = (id, event_id, tx_uuid, tx_id, tx_depth, tx_seq, event, id_string, type_string)
  }

  val transaction = TableQuery[Transaction]
  val event = TableQuery[Event]

  /*
  create table transaction(
    tx_uuid bigint not null,
    tx_id bigint not null,
    tx_size integer not null,
    primary key(tx_uuid, tx_id)
  );

  create table event(
    id bytea not null,
    event_id bigint not null,
    tx_uuid bigint not null,
    tx_id bigint not null,
    tx_depth integer not null,
    tx_seq integer not null,
    event bytea not null,
    id_string text,
    type_string text,
    primary key(id, event_id)
  );
  */
}
