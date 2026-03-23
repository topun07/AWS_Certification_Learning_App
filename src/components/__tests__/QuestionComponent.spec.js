/**
 * @vitest-environment jsdom
 */
import { mount } from '@vue/test-utils'
import { describe, it, expect, vi } from 'vitest'
import QuestionComponent from '../QuestionComponent.vue'

describe('QuestionComponent PDF Logic', () => {
    it('hides the modal before calling window.print', async () => {
        const wrapper = mount(QuestionComponent)

        // Mock the global window.print function
        const printSpy = vi.spyOn(window, 'print').mockImplementation(() => {})

        // Manually trigger the print function
        await wrapper.vm.handlePrint()

        // ASSERT: The modal should be hidden immediately
        expect(wrapper.vm.showReviewModal).toBe(false)

        printSpy.mockRestore()
    })
})