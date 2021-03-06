import { Component } from '@angular/core';
import { IonicPage, NavController, AlertController } from 'ionic-angular';
import { ContasServicoProvider } from '../../providers/contas-servico/contas-servico';
import { NativeStorage } from '@ionic-native/native-storage';
import { HomePage } from '../home/home';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';


@IonicPage()
@Component({
  selector: 'page-cadastro-usuario',
  templateUrl: 'cadastro-usuario.html',
})
export class CadastroUsuarioPage {
  user = {};
  registerForm: FormGroup;

  constructor(public navCtrl: NavController, public accountService: ContasServicoProvider,
    public storage: NativeStorage,
    public formBuilder: FormBuilder,
    public alertCtrl: AlertController) {

    this.registerForm = formBuilder.group({
      name: ['', Validators.compose([Validators.required])],
      Email: ['', Validators.compose([Validators.required, Validators.email])],
      PhoneNumber: [''],
      Password: ['', Validators.compose([Validators.required])],
      ConfirmPassword: ['', Validators.compose([Validators.required])],
      City: ['']
    });

  }

  ionViewDidLoad() {
    console.log('ionViewDidLoad CadastroUsuarioPage');
  }

  registerUser() {
    if (this.registerForm.controls.Password.value == this.registerForm.controls.ConfirmPassword.value) {
      if (this.registerForm.valid) {
        console.log('INFO - form válido ' + JSON.stringify(this.user));
        this.accountService.registerUser(this.user)
          .subscribe(
          data => {
            console.log('INFO - registerUser > CadastroUsuarioPage ' + JSON.stringify(data));
            //retorna ok, redireciona para o endpoint token e salva accesToken e refreshToken
            this.accountService.login(this.user)
              .subscribe(
              data => {
                console.log('INFO - login > registerUser > CadastroUsuarioPage ' + JSON.stringify(data));
                //salva acces token e refresh token
                this.storage.setItem('access_token', data.access_token);
                this.storage.setItem('refresh_token', data.refresh_token);
                this.storage.setItem('username',data.userName)
                this.navCtrl.setRoot(HomePage);
              },
              err => {
                console.log('ERROR - login > registerUser > CadastroUsuarioPage ' + JSON.stringify(err));
              }
              )
          },
          err => {
            console.log('ERROR - registerUser > CadastroUsuarioPage ' + JSON.stringify(err));
          }
          )
      } else {
        let alert = this.alertCtrl.create({
          title: 'Inválido',
          subTitle: 'Por favor preencha todos os campos obrigatórios.',
          buttons: ['OK']
        });
        alert.present();
      }
    }else{
      let alert = this.alertCtrl.create({
          title: 'Senhas não são iguais',
          subTitle: 'O campo de confirmar senha não está igual.',
          buttons: ['OK']
        });
        alert.present();
    }

    
  }

}
