package hmv;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;


/** binds an ObjectProperty bidirectional to a ToggleGroup.
 * Relies on the fact that ToggleGroup does not raise ChangeEvent when a toggle is already selected.
 *
 *
 * @param <T>
 */
public class ToggleGroupBinding<T> {

    public ToggleGroupBinding(final ToggleGroup toggleGroup, final ObjectProperty<T> property) {
        // Check all toggles for required user data
        toggleGroup.getToggles()
                   .forEach(toggle -> {
                       if (toggle.getUserData() == null) {
                           throw new IllegalArgumentException(
                                   "The ToggleGroup contains at least one Toggle without user data!");
                       }
                   });
        // Select initial toggle for current property state
        reflectObjectstateOnToggleGroup(toggleGroup, property);
        // Update property value on toggle selection changes
        toggleGroup.selectedToggleProperty()
                   .addListener((observable, oldValue, newValue) -> {
                       property.setValue((T) newValue.getUserData());
                   });

        property.addListener((observable, oldValue, newValue) -> {
                       reflectObjectstateOnToggleGroup(toggleGroup, property);
                   });

    }

    private void reflectObjectstateOnToggleGroup(final ToggleGroup toggleGroup, final ObjectProperty<T> property) {
        for (Toggle toggle : toggleGroup.getToggles()) {
            if ( toggle.getUserData().equals(property.getValue())) {
                toggleGroup.selectToggle(toggle);
                break;
            }
        }
    }
};
