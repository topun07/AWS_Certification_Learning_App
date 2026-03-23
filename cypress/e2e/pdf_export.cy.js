describe('PDF Export Flow', () => {
    it('completes 9 questions and triggers the PDF', () => {
        // 1. Visit the app
        cy.visit('http://localhost:5173');

        // 2. FAIL-SAFE LOGIN: Only types if the modal actually exists
        cy.get('body').then(($body) => {
            if ($body.find('input[placeholder="Your Email"]').length > 0) {
                cy.get('input[placeholder="Your Email"]').type('test@example.com');
                cy.contains('button', 'Log In').click();
                // Wait for modal to vanish
                cy.get('input[placeholder="Your Email"]').should('not.exist');
            }
        });

        // 3. Select AI Practitioner
        cy.contains('AI Practitioner', { timeout: 10000 }).should('be.visible').click();

        // 4. THE 9-QUESTION LOOP
        for (let n = 1; n <= 9; n++) {
            // Select first answer option (ignoring header/nav buttons)
            cy.get('button')
                .filter(':not(:contains("Submit"))')
                .filter(':not(:contains("Practitioner"))')
                .filter(':not(:contains("Pause"))')
                .filter(':not(:contains("Grade"))')
                .first()
                .click({ force: true });

            // Click Submit Answer
            cy.contains('Submit Answer').should('be.visible').click({ force: true });

            // Click Next Question (Force used due to double explanations in Image 10)
            cy.contains('Next Question ➔', { timeout: 5000 })
                .should('be.visible')
                .click({ force: true });

            cy.wait(400);
        }

        // 5. Click the Header Button (Grade Now 🏁)
        // We look for the one in the blue header we just added
        cy.contains('button', 'Grade Now 🏁').should('be.visible').click({ force: true });

        // 6. Handle the Browser Alert
        cy.on('window:confirm', () => true);

        // 7. Click "Save Results as PDF"
        cy.contains(/Exam Results/i, { timeout: 10000 }).should('be.visible');
        cy.contains('Save Results as PDF').should('be.visible').click({ force: true });

        // Final Verification
        cy.get('#final-pdf-report', { timeout: 5000 }).should('exist');
    });
});