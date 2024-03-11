describe('Home page', () => {
	it('Show the home page', () => {
		cy.visit('/')
		cy.assertTitle('Home')
	})
})
