package rs.dk150.cryptotracker.data

/** interface allowing generalization of response POJO */
interface ResponsePOJO {
    val response: String?
    val message: String?
}