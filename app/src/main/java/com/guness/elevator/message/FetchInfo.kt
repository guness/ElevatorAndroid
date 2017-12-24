package com.guness.elevator.message

import com.guness.elevator.model.Fetch

/**
 * Created by guness on 24.12.2017.
 */
class FetchInfo() : AbstractMessage() {
    var fetch: Fetch? = null

    constructor(fetch: Fetch) : this() {
        this.fetch = fetch
    }
}