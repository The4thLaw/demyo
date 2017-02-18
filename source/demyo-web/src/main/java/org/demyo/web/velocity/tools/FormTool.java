package org.demyo.web.velocity.tools;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
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
	 * @param <T>
	 *            The type of value
	 * @return The option string to output.
	 */
	@SuppressWarnings("unchecked")
	public <T> String selectOptions(List<T> possibleValues, Object selectedValues) {
		if (CollectionUtils.isEmpty(possibleValues)) {
			return "";
		}
		if (possibleValues.get(0) instanceof IModel) {
			return selectOptionsForModel((List<IModel>) possibleValues, selectedValues);
		}

		return selectOptionsForMap(possibleValues, selectedValues);
	}

	// Intentionally does not support multiple selection at the moment, as we have no use for it
	private <T> String selectOptionsForMap(List<T> possibleValues, Object selectedValues) {
		// Gather the selected values
		String selectedId = null;
		if (selectedValues instanceof String) {
			selectedId = (String) selectedValues;
		} else if (selectedValues != null) {
			throw new DemyoRuntimeException(DemyoErrorCode.WEB_FORM_INVALID_OPTIONS,
					"Cannot handle options of type " + selectedValues.getClass());
		}

		StringBuilder sb = new StringBuilder();

		for (T possibleValue : possibleValues) {
			String id;
			String name;

			if (possibleValue instanceof Map<?, ?>) {
				Map<?, ?> model = (Map<?, ?>) possibleValue;
				id = (String) model.get("id");
				name = (String) model.get("identifyingName");
			} else {
				throw new DemyoRuntimeException(DemyoErrorCode.WEB_FORM_INVALID_OPTIONS,
						"Cannot handle options of type " + possibleValue.getClass());
			}

			sb.append("<option value='").append(id).append("'");
			if (id.equals(selectedId)) {
				sb.append("selected='selected'");
			}
			sb.append('>').append(name).append("</option>");
		}

		return sb.toString();
	}

	private String selectOptionsForModel(List<IModel> possibleValues, Object selectedValues) {
		// Gather the selected values
		Set<Long> selectedIds = new HashSet<>();
		if (selectedValues instanceof String) {
			selectedIds.add(Long.parseLong((String) selectedValues));
		} else if (selectedValues instanceof Collection) {
			for (Object item : (Collection<?>) selectedValues) {
				selectedIds.add(((IModel) item).getId());
			}
		} else if (selectedValues != null) {
			throw new DemyoRuntimeException(DemyoErrorCode.WEB_FORM_INVALID_OPTIONS,
					"Cannot handle options of type " + selectedValues.getClass());
		}

		StringBuilder sb = new StringBuilder();

		for (IModel possibleValue : possibleValues) {
			long id = possibleValue.getId();
			String name = possibleValue.getIdentifyingName();

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
