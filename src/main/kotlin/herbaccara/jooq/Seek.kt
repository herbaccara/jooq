package herbaccara.jooq

data class Seek<E>(val hasNext: Boolean, val content: List<E>)
