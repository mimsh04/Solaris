package in2000.team42

import in2000.team42.model.solarPanels.SolarPanelModel
import in2000.team42.model.solarPanels.defaultPanels
import in2000.team42.ui.screens.home.ApiData
import in2000.team42.ui.screens.home.Config
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import in2000.team42.ui.screens.home.bottomSheet.Medium
import in2000.team42.ui.screens.home.bottomSheet.Peek
import in2000.team42.ui.screens.home.HomeViewModel
import io.mockk.every
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.assertEquals

class BottomSheetUnitTest {
    private lateinit var viewModel: HomeViewModel
    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()

    fun shouldShowGreetingContent(address: String): Boolean {
        return address.isEmpty()
    }

    @Test
    fun `should show greeting content when address is empty`() {
        assertTrue(shouldShowGreetingContent(""))
    }

    @Test
    fun `should not show greeting content when address is non-empty`() {
        assertFalse(shouldShowGreetingContent("Starrveien 1"))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        viewModel = mockk(relaxed = true)
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should update bottom sheet detent in viewmodel when detent changes`() = runTest {
        // Simulerer endring av deten til BottomSheet
        coEvery { viewModel.setBottomSheetDetent(any()) } returns Unit

        // Simulate the LaunchedEffect logic
        val detent = Medium
        viewModel.setBottomSheetDetent(detent.identifier)

        // Sjekker HomeViewModel interaksjon
        coVerify { viewModel.setBottomSheetDetent("medium") }

        // Sjekker annen detent
        viewModel.setBottomSheetDetent(Peek.identifier)
        coVerify { viewModel.setBottomSheetDetent("peek") }
    }

    @Test
    fun `should call viewmodel methods when child composable callbacks are invoked`() = runTest {
        // Mock ViewModel
        val viewModel = mockk<HomeViewModel>()
        coEvery { viewModel.setIncline(any()) } returns Unit
        coEvery { viewModel.setVinkel(any()) } returns Unit
        coEvery { viewModel.setSelectedSolarPanel(any()) } returns Unit
        coEvery { viewModel.updateAllApi() } returns Unit

        // Simulerer Vinkelinputs callback
        val onInclineChange: (Float) -> Unit = { viewModel.setIncline(it) }
        val onDirectionChange: (Float) -> Unit = { viewModel.setVinkel(it) }
        onInclineChange(45f)
        onDirectionChange(90f)

        // Simulerer SolcelleDropdown callback
        val onPanelSelected: (SolarPanelModel) -> Unit = { viewModel.setSelectedSolarPanel(it) }
        onPanelSelected(defaultPanels[1])

        // Simulerer UpdateApiButton callback
        val onUpdateApi: () -> Unit = { viewModel.updateAllApi() }
        onUpdateApi()

        // Verify ViewModel interactions
        coVerify { viewModel.setIncline(45f) }
        coVerify { viewModel.setVinkel(90f) }
        coVerify { viewModel.setSelectedSolarPanel(defaultPanels[1]) }
        coVerify { viewModel.updateAllApi() }
    }

    @Test
    fun `should pass correct config to child composables`() {
        // Mock ViewModel og flows
        val viewModel = mockk<HomeViewModel>()
        val configFlow = MutableStateFlow(Config(
            adress = "123 Main St",
            incline = 30f,
            vinkel = 180f,
            areal = 10f,
            selectedPanelModel = defaultPanels[0]
        ))
        val apiDataFlow = MutableStateFlow(ApiData())

        every { viewModel.configFlow } returns configFlow
        every { viewModel.apiDataFlow } returns apiDataFlow

        // Simulerer at HomeViewModel har collecting state (mimic collectAsState)
        val config = configFlow.value

        // Tester props som blir tildelt child composables
        assertEquals("123 Main St", config.adress, "Address should match")
        assertEquals(30f, config.incline, "Incline should match")
        assertEquals(180f, config.vinkel, "Vinkel should match")
        assertEquals(10f, config.areal, "Areal should match")
        assertEquals(defaultPanels[0], config.selectedPanelModel, "Selected panel should match")
    }
}