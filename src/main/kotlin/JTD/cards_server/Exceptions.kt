package JTD.cards_server


open class UnexpectedResponse(actual: Any?, expected: Any?)
    : Exception("Expected $expected but got $actual.")