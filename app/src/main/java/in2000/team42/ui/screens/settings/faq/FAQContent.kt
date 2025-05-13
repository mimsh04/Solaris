package in2000.team42.ui.screens.settings.faq

import in2000.team42.R

data class FAQ(
    val questionResId: Int,
    val answerResId: Int
)

val faqItems = listOf(
    FAQ(R.string.faq1_question, R.string.faq1_answer),
    FAQ(R.string.faq2_question, R.string.faq2_answer),
    FAQ(R.string.faq3_question, R.string.faq3_answer),
    FAQ(R.string.faq4_question, R.string.faq4_answer),
    FAQ(R.string.faq5_question, R.string.faq5_answer)
)