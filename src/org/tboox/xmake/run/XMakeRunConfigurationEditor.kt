package org.tboox.xmake.run

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.RawCommandLineEditor
import com.intellij.ui.layout.*
import com.intellij.ui.components.Label
import java.awt.Dimension
import java.nio.file.Path
import java.nio.file.Paths
import javax.swing.JComponent
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.DefaultComboBoxModel
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener

class XMakeRunConfigurationEditor(private val project: Project) : SettingsEditor<XMakeRunConfiguration>() {

    // the targets ui
    private val targetsModels = DefaultComboBoxModel<String>()
    private val targetsComboBox = ComboBox<String>(targetsModels)

    // the platforms ui
    private val platformsModels = DefaultComboBoxModel<String>()
    private val platformsComboBox = ComboBox<String>(platformsModels)

    // the architectures ui
    private val architecturesModels = DefaultComboBoxModel<String>()
    private val architecturesComboBox = ComboBox<String>(architecturesModels)

    // the modes ui
    private val modesModels = DefaultComboBoxModel<String>()
    private val modesComboBox = ComboBox<String>(modesModels)

    // the additional configuration
    private val additionalConfiguration = RawCommandLineEditor()

    // reset editor from configuration
    override fun resetEditorFrom(configuration: XMakeRunConfiguration) {

        // reset targets
        targetsModels.removeAllElements()
        for (target in configuration.targets) {
            targetsModels.addElement(target)
        }
        targetsModels.selectedItem = configuration.currentTarget

        // reset platforms
        platformsModels.removeAllElements()
        for (platform in configuration.platforms) {
            platformsModels.addElement(platform)
        }
        platformsModels.selectedItem = configuration.currentPlatfrom

        // reset architectures
        architecturesModels.removeAllElements()
        for (architecture in configuration.architectures) {
            architecturesModels.addElement(architecture)
        }
        architecturesModels.selectedItem = configuration.currentArchitecture

        // reset modes
        modesModels.removeAllElements()
        for (mode in configuration.modes) {
            modesModels.addElement(mode)
        }
        modesModels.selectedItem = configuration.currentMode

        // reset additional configuration
        additionalConfiguration.text = configuration.additionalConfiguration
    }

    // apply editor to configuration
    override fun applyEditorTo(configuration: XMakeRunConfiguration) {

        configuration.currentTarget             = targetsModels.selectedItem.toString()
        configuration.currentPlatfrom           = platformsModels.selectedItem.toString()
        configuration.currentArchitecture       = architecturesModels.selectedItem.toString()
        configuration.currentMode               = modesModels.selectedItem.toString()
        configuration.additionalConfiguration   = additionalConfiguration.text
    }

    // create editor
    override fun createEditor(): JComponent = panel {

        labeledRow("Default target:", targetsComboBox) {
            targetsComboBox(CCFlags.push)
        }

        labeledRow("Platform:", platformsComboBox) {
            platformsComboBox(CCFlags.push)
        }

        labeledRow("Architecture:", architecturesComboBox) {
            architecturesComboBox(CCFlags.push)
        }

        labeledRow("Mode:", modesComboBox) {
            modesComboBox(CCFlags.push)
        }

        labeledRow("Additional configuration:", additionalConfiguration) {
            additionalConfiguration.apply {
                dialogCaption = "Additional configuration"
                makeWide()
            }()
        }

        platformsModels.addListDataListener(object: ListDataListener {
            override fun contentsChanged(e: ListDataEvent) {
                architecturesModels.removeAllElements()
                for (architecture in XMakeRunConfiguration.getArchitecturesByPlatform(platformsModels.selectedItem.toString())) {
                    architecturesModels.addElement(architecture)
                }
            }
            override fun intervalAdded(e: ListDataEvent) {
            }
            override fun intervalRemoved(e: ListDataEvent) {
            }
        })
    }

    private fun JPanel.makeWide() {
        preferredSize = Dimension(1000, height)
    }

    private fun LayoutBuilder.labeledRow(labelText: String, component: JComponent, init: Row.() -> Unit) {
        val label = Label(labelText)
        label.labelFor = component
        row(label) { init() }
    }

    companion object {

        // get log
        private val Log = Logger.getInstance(XMakeRunConfigurationEditor::class.java.getName())
    }
}