package hu.levente.fazekas.receiptscannerbackend.model

import jakarta.persistence.*

@Entity
class Receipt (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var receiptId: Long = 0,

    var clientId: Long,

    var name: String,

    var date: Long,

    var currency: String,

    var sumOfPrice: Long,

    var description: String,

    var imageUri: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_userId")
    var user: User,

    var tags: String,

    var items: String,

    var syncVersion: Long
)