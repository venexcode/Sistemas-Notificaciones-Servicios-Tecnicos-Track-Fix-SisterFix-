// Mientras se escribe el telefono o el email, consulta si el cliente ya existe y, si es asi, completa sus datos.
(function () {
  var form = document.getElementById('form-registro');
  if (!form) return;

  var tel = document.getElementById('telefono');
  var email = document.getElementById('email');
  var nombre = document.getElementById('nombre');
  var nota = document.getElementById('cliente-nota');
  var btnEditar = document.getElementById('btn-editar');
  var url = form.getAttribute('data-buscar-url');
  var espera, ultimaClave = '', bloqueado = false;

  function soltar() {
    if (!bloqueado) return;
    bloqueado = false;
    nombre.readOnly = false;
    email.readOnly = false;
    nota.hidden = true;
    btnEditar.hidden = true;
  }

  function mostrar(cliente) {
    bloqueado = true;
    nombre.value = cliente.nombre || '';
    if (cliente.email) email.value = cliente.email;
    nombre.readOnly = true;
    email.readOnly = !!cliente.email;
    nota.textContent = '';
    var fuerte = document.createElement('strong');
    fuerte.textContent = 'Cliente ya registrado: ' + cliente.nombre + '.';
    nota.appendChild(fuerte);
    nota.appendChild(document.createTextNode(' Se usará su ficha.'));
    nota.hidden = false;
    btnEditar.hidden = false;
  }

  function consultar() {
    var t = tel.value.replace(/[^0-9+]/g, '');
    var e = email.value.trim();
    if (t.length < 6 && !e) { soltar(); ultimaClave = ''; return; }

    var clave = t + '|' + e;
    if (clave === ultimaClave) return;
    ultimaClave = clave;

    fetch(url + '?telefono=' + encodeURIComponent(t) + '&email=' + encodeURIComponent(e),
          { headers: { 'Accept': 'application/json' } })
      .then(function (r) { return r.status === 200 ? r.json() : null; })
      .then(function (cliente) { cliente ? mostrar(cliente) : soltar(); })
      .catch(function () { soltar(); });
  }

  function programar() {
    clearTimeout(espera);
    espera = setTimeout(consultar, 350);
  }

  btnEditar.addEventListener('click', function (e) {
    e.preventDefault();
    bloqueado = false;
    nombre.readOnly = false;
    email.readOnly = false;
    nombre.focus();
    btnEditar.hidden = true;
  });

  tel.addEventListener('input', function () {
    soltar();
    programar();
  });

  email.addEventListener('input', function () {
    soltar();
    programar();
  });

  tel.addEventListener('blur', consultar);
  email.addEventListener('blur', consultar);
  if (tel.value) consultar();
})();