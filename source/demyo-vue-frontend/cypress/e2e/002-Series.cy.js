describe('Series', () => {
	it('Create a series', () => {
		// Create the series
		cy.visit('/series/new')
		cy.getInputByLabel('Name').type('Sample series')
		cy.get('button[type="submit"]').click()

		// Ensure the page was reloaded
		cy.url().should('match', /series\/\d+\/view/)
		// Ensure that the encoded data is correct
		cy.assertTitle('Sample series')
		cy.contains('This series is ongoing').should('exist')
	})

	it('Deletes an empty series', () => {
		cy.visit('/series/2/view')
		cy.openQuickTasks()
		cy.contains('Delete this series').click()
		cy.get('.v-dialog .v-btn.accent').click()
		cy.url().should('match', /series$/)
		cy.contains('Empty series').should('not.exist')
	})

	// TODO: https://github.com/The4thLaw/demyo/issues/93
})
