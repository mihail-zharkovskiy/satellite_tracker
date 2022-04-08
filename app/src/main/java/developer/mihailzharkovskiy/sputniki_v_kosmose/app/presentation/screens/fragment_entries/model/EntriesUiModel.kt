package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_entries.model

data class EntriesUiModel(
    /**id satellite**/
    val id: Int,
    /**name satellite**/
    val name: String,
    var isSelected: Boolean,
)

