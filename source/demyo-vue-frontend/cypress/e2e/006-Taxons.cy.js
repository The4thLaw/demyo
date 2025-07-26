describe('Taxons', () => {
	it('List the taxons', () => {
		cy.visit('/taxons')
		// Taxons should be listed without error
		cy.get('.d-Taxon').should('have.length', 1)
		// The SF taxon should have one occurrence
		cy.contains('science-fiction').find('.d-Taxon__count').should('have.text', '2')
	})

	it('Views a taxon', () => {
		cy.visit('/taxons/1/view')
		cy.assertTitle('science-fiction')
	})
})
