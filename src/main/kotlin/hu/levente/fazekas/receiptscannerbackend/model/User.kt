package hu.levente.fazekas.receiptscannerbackend.model

import jakarta.persistence.*

@Entity
class User (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var userId: Long = 0,

    var name: String,

    var email: String,

    var password: String = "",

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = [CascadeType.ALL], targetEntity = Receipt::class)
    var receipts: List<Receipt> = emptyList()
)