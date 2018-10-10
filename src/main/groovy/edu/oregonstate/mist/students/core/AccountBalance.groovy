package edu.oregonstate.mist.students.core

import edu.oregonstate.mist.students.db.BackendAccountBalance

class AccountBalance {
    BigDecimal currentBalance

    static AccountBalance fromBackendAccountBalance(BackendAccountBalance backendAccountBalance) {
        new AccountBalance(
                currentBalance: backendAccountBalance.balance
        )
    }
}
