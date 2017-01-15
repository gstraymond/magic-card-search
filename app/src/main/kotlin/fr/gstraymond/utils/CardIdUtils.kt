package fr.gstraymond.utils

import fr.gstraymond.models.search.response.Card

fun Card.getId() = "$title $type $castingCost"