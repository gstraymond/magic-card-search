package fr.gstraymond.models

data class HelpText(val title: String,
                    val descriptions: List<String>,
                    val items: List<String>,
                    val texts: List<HelpText>)
