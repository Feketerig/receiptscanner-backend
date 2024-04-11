package hu.levente.fazekas.receiptscannerbackend.model

import jakarta.persistence.*

@Entity
class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var token: String,

    var expirationDate: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_userId")
    var user: User
)