package timelimit.gallery

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import timelimit.account.Users

@RestController
class EditController {
    class Edit constructor(val status: String)

    @RequestMapping("/gallery/edit")
    fun edit(@RequestParam(value = "art_id") art_id: Int, @RequestParam(value = "data") data: String, @RequestParam(value = "token") token: String) : Edit {
        if (token.length != 32) {
            return Edit("FAIL")
        }
        
        var status = "FAIL"
        transaction {
            val query = Users.select { Users.token eq token }
            if (query.count() != 1) {
                return@transaction
            }
            val userId = query.iterator().next()[Users.user_id]

            if (Gallery.select { (Gallery.user_id eq userId) and (Gallery.art_id eq art_id) }.count() != 1) {
                return@transaction
            }

            if (Gallery.update(where = { (Gallery.user_id eq userId) and (Gallery.art_id eq art_id) }) {
                it[Gallery.data] = data
            } != 1) {
                return@transaction
            }
            status = "OK"
        }

        return Edit(status)
    }
}