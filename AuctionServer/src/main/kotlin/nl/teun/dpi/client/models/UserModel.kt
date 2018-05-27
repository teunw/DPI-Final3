package nl.teun.dpi.client.models

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import nl.teun.dpi.common.data.User
import tornadofx.ItemViewModel

class UserModel(private val user:User = User(username = "")):ItemViewModel<User>(user) {
    val username = bind { SimpleStringProperty(item.username) }
}