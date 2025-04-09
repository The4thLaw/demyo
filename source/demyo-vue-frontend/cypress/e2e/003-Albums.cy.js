describe('Albums', () => {
	// See https://github.com/The4thLaw/demyo/issues/6
	it('Show 0-numbered albums', () => {
		cy.visit('/albums/2/view')
		cy.contains('0 - Album with number 0').should('exist')
	})

	// See https://github.com/The4thLaw/demyo/issues/16
	it('Show authors when there is only an inker', () => {
		cy.visit('/albums/3/view')
		cy.contains('Sample Inker').should('exist')
	})

	// See https://github.com/The4thLaw/demyo/issues/16
	it('Show authors when there is only a translator', () => {
		cy.visit('/albums/4/view')
		cy.contains('Sample Translator').should('exist')
	})

	// See https://github.com/The4thLaw/demyo/issues/77
	it('Show an error message when creating without a publisher', () => {
		cy.visit('/albums/new')
		cy.get('.c-FormActions__submit').click()
		cy.contains('Publisher').parents('.v-input').contains('This field cannot be empty')
	})

	it('Views an album', () => {
		cy.visit('/albums/1/view')
		cy.assertTitle('À Feu et à Cendres')
	})

	// See https://github.com/The4thLaw/demyo/issues/88
	it('Prevents deletion of an album with a derivative', () => {
		cy.visit('/albums/1/view')
		cy.openQuickTasks()
		cy.contains('Delete this album').should('not.exist')
	})
})
