package org.demyo.web.velocity.tools;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.ValidScope;
import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoRuntimeException;
import org.demyo.model.IModel;

/**
 * Velocity to ease HTML form management.
 */
@ValidScope(Scope.APPLICATION)
public class FormTool {

	/**
	 * Output the &lt;select> &lt;option>s for forms.
	 * <p>
	 * For a simple loop like this, pure Java code is more efficient than parsing the VM. This should be more noticeable
	 * on limited hardware like a Raspberry Pi.
	 * </p>
	 * <p>
	 * On good hardware, the time still goes from ~600ms to ~250ms on the Album edit page.
	 * </p>
	 * 
	 * @param possibleValues
	 *            The possible values for the options.
	 * @param selectedValues
	 *            The values actually selected.
	 * @return The option string to output.
	 */
	public <T> String selectOptions(List<T> possibleValues, Object selectedValues) {
		// Gather the selected values
		Set<Object> selectedIds = new HashSet<Object>();
		if (selectedValues instanceof String) {
			try {
				selectedIds.add(Long.parseLong((String) selectedValues));

			} catch (NumberFormatException e) {
				selectedIds.add(selectedValues);
			}
		} else if (selectedValues instanceof Collection) {
			for (Object item : (Collection<?>) selectedValues) {
				if (!(item instanceof IModel)) {
					throw new DemyoRuntimeException(DemyoErrorCode.WEB_FORM_INVALID_OPTIONS,
							"Cannot handle options of type " + selectedValues.getClass());
				}
				selectedIds.add(((IModel) item).getId());
			}
		} else if (selectedValues != null) {
			throw new DemyoRuntimeException(DemyoErrorCode.WEB_FORM_INVALID_OPTIONS,
					"Cannot handle options of type " + selectedValues.getClass());
		}

		StringBuilder sb = new StringBuilder();

		for (T possibleValue : possibleValues) {
			Object id;
			String name;

			if (possibleValue instanceof IModel) {
				IModel model = (IModel) possibleValue;
				id = model.getId();
				name = model.getIdentifyingName();
			} else if (possibleValue instanceof Map<?, ?>) {
				Map<?, ?> model = (Map<?, ?>) possibleValue;
				id = model.get("id");
				name = (String) model.get("identifyingName");
			} else {
				throw new DemyoRuntimeException(DemyoErrorCode.WEB_FORM_INVALID_OPTIONS,
						"Cannot handle options of type " + possibleValue.getClass());
			}

			boolean selected = selectedIds.contains(id);
			sb.append("<option value='").append(id).append("'");
			if (selected) {
				sb.append("selected='selected'");
			}
			sb.append('>').append(name).append("</option>");
		}

		return sb.toString();
	}

}
