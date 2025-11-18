/**
 * Utility function to trigger file download
 * @param blob - The Blob object containing the file data
 * @param filename - The name of the file to be downloaded
 */
export function downloadFile(blob: Blob, filename: string): void {
  // Create a temporary URL for the blob
  const url = window.URL.createObjectURL(blob)
  
  // Create a temporary anchor element
  const a = document.createElement('a')
  a.style.display = 'none'
  a.href = url
  a.download = filename
  
  // Append to body, click and remove
  document.body.appendChild(a)
  a.click()
  
  // Cleanup
  window.URL.revokeObjectURL(url)
  document.body.removeChild(a)
}

/**
 * Helper function to download content as a file
 * @param content - The content to download
 * @param filename - The name of the file
 * @param contentType - The MIME type of the file
 */
export function downloadContent(
  content: string,
  filename: string,
  contentType = 'text/plain;charset=utf-8;'
): void {
  const blob = new Blob([content], { type: contentType })
  downloadFile(blob, filename)
}

/**
 * Helper function to download JSON as a file
 * @param data - The JSON data to download
 * @param filename - The name of the file (without .json extension)
 */
export function downloadJson(data: any, filename: string): void {
  const content = JSON.stringify(data, null, 2)
  downloadContent(content, `${filename}.json`, 'application/json;charset=utf-8;')
}

/**
 * Helper function to download CSV content
 * @param csvContent - The CSV content to download
 * @param filename - The name of the file (without .csv extension)
 */
export function downloadCsv(csvContent: string, filename: string): void {
  // Add UTF-8 BOM for Excel compatibility with special characters
  const content = '\uFEFF' + csvContent
  downloadContent(content, `${filename}.csv`, 'text/csv;charset=utf-8;')
}
