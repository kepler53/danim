import React, { useState } from 'react';
import { observer } from 'mobx-react-lite';
import { useHistory } from 'react-router-dom';
import { SignInDto } from '../../model/sign-in-dto';
import InputValidator from '../../util/input-validator';
import {
  makeStyles,
  Button,
  Container,
  Typography,
  TextField,
  Grid,
  Snackbar,
} from '@material-ui/core';
import MuiAlert from '@material-ui/lab/Alert';
import useAuth from '../../hooks/useAuth';

const useStyles = makeStyles((theme) => ({
  paper: {
    marginTop: theme.spacing(8),
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
  },
  form: {
    width: '100%',
    marginTop: theme.spacing(1),
  },
  submit_button: {
    marginTop: theme.spacing(1),
    padding: theme.spacing(2),
    fontSize: '1rem',
  },
}));

function Alert(props) {
  return <MuiAlert elevation={6} variant="filled" {...props} />;
}

const SignIn = observer(() => {
  const classes = useStyles();
  const history = useHistory();
  const authStore = useAuth();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errorTextEmail, setErrorTextEmail] = useState('');
  const [errorTextPassword, setErrorTextPassword] = useState('');
  const [snackbarInfo, setSnackbarInfo] = useState({
    isShow: false,
    msg: '',
    state: '',
  });

  const checkEmail = (e) => {
    const email = e.target.value;
    setEmail(email);
    if (InputValidator.checkEmail(email)) {
      setErrorTextEmail('');
    } else {
      setErrorTextEmail('올바른 이메일을 입력해주세요.');
    }
  };

  const checkPassword = (e) => {
    const password = e.target.value;
    setPassword(password);
    if (InputValidator.checkPassword(password)) {
      setErrorTextPassword('');
    } else {
      setErrorTextPassword('비밀번호는 영문,숫자 포함 8~12글자입니다.');
    }
  };

  const signInFormCheck = () => {
    if (
      errorTextEmail === '' &&
      errorTextPassword === '' &&
      email &&
      password
    ) {
      return true;
    }
    return false;
  };

  const handleSignIn = async (e) => {
    e.preventDefault();
    if (!signInFormCheck()) {
      console.log('empty input');
      setSnackbarInfo({
        isShow: true,
        msg: '올바른 정보를 기입해주세요.',
        state: 'error',
      });
      return;
    }
    await authStore.signIn(new SignInDto(email, password));
    if (authStore.isLoggedIn) {
      setSnackbarInfo({
        isShow: true,
        msg: '로그인 성공',
        state: 'success',
      });
      history.push('/');
      return;
    }
    setSnackbarInfo({
      isShow: true,
      msg: '아이디나 비밀번호가 틀렸습니다.',
      state: 'error',
    });
  };

  const handleClose = (_, reason) => {
    if (reason === 'clickaway') {
      return;
    }
    setSnackbarInfo({
      ...snackbarInfo,
      isShow: false,
    });
  };

  const goToFindPassword = () => {
    history.push('/findPassword');
  };

  const goToSignUp = () => {
    history.push('/signup');
  };

  return (
    <Container component="main" maxWidth="xs">
      <div className={classes.paper}>
        <Typography component="h3" variant="h3" align="center">
          다님
        </Typography>
        <Typography component="h5" variant="h5" align="center">
          DANIM
        </Typography>
        <form className={classes.form}>
          <TextField
            required
            id="email"
            fullWidth
            type="email"
            variant="outlined"
            margin="normal"
            label="Email ID"
            autoComplete="email"
            autoFocus
            onChange={checkEmail}
            error={errorTextEmail !== '' ? true : false}
            helperText={errorTextEmail}
          />
          <TextField
            required
            id="password"
            fullWidth
            type="password"
            variant="outlined"
            margin="normal"
            label="Password"
            autoComplete="current-password"
            onChange={checkPassword}
            error={errorTextPassword !== '' ? true : false}
            helperText={errorTextPassword}
          />

          <Button
            className={classes.submit_button}
            onClick={handleSignIn}
            fullWidth
            variant="contained"
            color="primary"
          >
            로그인
          </Button>
          <Grid container justifyContent="space-between">
            <Button onClick={goToFindPassword}>비밀번호 찾기</Button>
            <Button color="primary" onClick={goToSignUp}>
              회원가입
            </Button>
          </Grid>
          <Snackbar
            open={snackbarInfo.isShow}
            autoHideDuration={700}
            onClose={handleClose}
          >
            <Alert onClose={handleClose} severity={snackbarInfo.state}>
              {snackbarInfo.msg}
            </Alert>
          </Snackbar>
        </form>
      </div>
    </Container>
  );
});

export default SignIn;