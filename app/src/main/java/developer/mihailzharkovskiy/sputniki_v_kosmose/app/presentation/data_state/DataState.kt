package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.data_state


data class DataState<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> succes(data: T): DataState<T> {
            return DataState(status = Status.SUCCESS, data = data, message = null)
        }

        fun <T> error(data: T?, message: String?): DataState<T> {
            return DataState(status = Status.ERROR, data = data, message = message)
        }

        fun <T> loading(data: T? = null): DataState<T> {
            return DataState(status = Status.LOADING, data = data, message = null)
        }

        fun <T> empty(data: T? = null): DataState<T> {
            return DataState(status = Status.EMPTY, data = data, message = null)
        }
    }
}

