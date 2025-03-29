describe('Tags', () => {
	it('List the tags', () => {
		cy.visit('/tags')
		// Tags should be listed without error
		cy.get('.d-Tag').should('have.length', 1)
		// The SF tag should have one occurrence
		cy.contains('science-fiction').find('.d-Tag__count').should('have.text', '2')
	})
})
