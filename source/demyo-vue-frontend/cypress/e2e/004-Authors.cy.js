describe('Authors', () => {
	it('Create an author', () => {
		// Create the author
		cy.visit('/authors/new')
		cy.getInputByLabel('First name').type('John')
		cy.getInputByLabel('Name').type('Doe')
		cy.get('button[type="submit"]').click()

		// Ensure the page was reloaded
		cy.url().should('match', /authors\/\d+\/view/)
		// Ensure that the encoded data is correct
		cy.assertTitle('John Doe')
		cy.contains('You have no albums by this author').should('exist')
		cy.contains('You have no derivatives by this author').should('exist')
	})

	it('Views an author', () => {
		cy.visit('/authors/1/view')
		cy.assertTitle('Jean-David Morvan')
	})
})
