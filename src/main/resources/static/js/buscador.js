// Pone el codigo en mayusculas y quita espacios y simbolos raros. La validacion real la hace el servidor.
document.querySelectorAll('[data-codigo]').forEach(function (input) {
  input.addEventListener('input', function () {
    input.value = input.value.toUpperCase().replace(/[^A-Z0-9-]/g, '');
  });
});