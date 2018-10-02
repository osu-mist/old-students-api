package edu.oregonstate.mist.students.core

import com.fasterxml.jackson.annotation.JsonFormat
import edu.oregonstate.mist.students.db.BackendAccountTransaction

import java.time.Instant
import java.time.ZonedDateTime

class AccountTransactions {
    List<Transaction> transactions

    static AccountTransactions fromBackendAccountTransactions(
            List<BackendAccountTransaction> backendAccountTransactions) {
        new AccountTransactions(transactions: backendAccountTransactions.collect {
            new Transaction(
                    amount: it.amount,
                    description: it.description,
                    entryDate: it.entryDate
            )
        })
    }
}

class Transaction {
    BigDecimal amount
    String description

    @JsonFormat(shape=JsonFormat.Shape.STRING, timezone="UTC")
    Instant entryDate
}
